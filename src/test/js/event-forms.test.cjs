const { test } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const path = require('node:path');

for (const page of ['create_event', 'edit_event', 'manage_events']) {
    function mount(fetch) {
        let submit;
        const state = { visible: false, removed: false, alerts: [] };
        const button = { disabled: false, dataset: { eventId: '1' } };
        const error = { textContent: '', classList: {
            add() { state.visible = false; }, remove() { state.visible = true; }
        } };
        const form = { dataset: { eventId: '1' }, querySelector: () => button,
            addEventListener: (_, handler) => { submit = handler; } };
        const context = { fetch, FormData: class {}, confirm: () => true,
            alert: message => state.alerts.push(message), window: { location: {} },
            document: { getElementById: id => {
                if (id === 'errorMessage') return error;
                if (id.endsWith('Form')) return form;
                if (id.startsWith('event-row-')) return { remove() { state.removed = true; } };
                return { value: 'csrf' };
            } }
        };
        const template = fs.readFileSync(path.join(__dirname, '../../main/resources/templates/admin', page + '.html'), 'utf8');
        vm.runInNewContext(template.match(/<script>([\s\S]*?)<\/script>/)[1], context);
        return { state, button, context, run: () => submit
            ? submit.call(form, { preventDefault() {} }) : context.deleteEvent(button) };
    }
    test(page + ': network failure is visible and restores button', async () => {
        const view = mount(async () => { throw new TypeError('Failed to fetch'); });
        await view.run();
        assert.ok(view.state.visible || view.state.alerts.length);
        assert.equal(view.button.disabled, false);
        assert.equal(view.state.removed, false);
        assert.equal(view.context.window.location.href, undefined);
    });
    test(page + ': HTTP failure is visible and restores button', async () => {
        const view = mount(async () => ({ ok: false, status: 403 }));
        await view.run();
        assert.ok(view.state.visible || view.state.alerts.length);
        assert.equal(view.button.disabled, false);
    });
    test(page + ': login redirect is not treated as success', async () => {
        const view = mount(async () => ({ ok: true, status: 200, redirected: true }));
        await view.run();
        assert.ok(view.state.visible || view.state.alerts.length);
        assert.equal(view.state.removed, false);
        assert.equal(view.context.window.location.href, undefined);
    });
    test(page + ': blocks duplicate requests while pending', async () => {
        let finish, calls = 0;
        const view = mount(() => { calls++; return new Promise(resolve => { finish = resolve; }); });
        const first = view.run();
        assert.equal(view.button.disabled, true);
        await view.run();
        assert.equal(calls, 1);
        finish({ ok: true, status: page === 'manage_events' ? 204 : 201 });
        await first;
        assert.equal(view.button.disabled, false);
        if (page === 'manage_events') assert.equal(view.state.removed, true);
        else assert.equal(view.context.window.location.href, '/admin/my-events');
    });
}
