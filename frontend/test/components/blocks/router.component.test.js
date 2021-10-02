/**
 * @jest-environment jsdom
 */

import {
  fixture,
  fixtureCleanup,
} from '@open-wc/testing-helpers/index-no-side-effects.js';
import Router from '../../../src/components/blocks/router.component.js';

class TestComponent extends HTMLElement {
  constructor() {
    super();
  }
}

describe('Router component', () => {
  let authState, router;

  beforeAll(() => {
    authState = {
      isAuthenticated: jest.fn(() => false),
    }
    router = {
      register: jest.fn(),
      deregister: jest.fn(),
      redirect: jest.fn()
    };
    window.customElements.define('t-router', Router(authState, router));
    window.customElements.define('t-test', TestComponent);
  });

  afterEach(() => {
    fixtureCleanup();
    jest.clearAllMocks();
  });

  it('should render', async () => {
    const el = await fixture('<t-router></t-router>');
    expect(el.shadowRoot.innerHTML).toMatchSnapshot();
  });

  it('should register itself with the router', async () => {
    const el = await fixture('<t-router></t-router>');
    expect(router.register).toHaveBeenCalledTimes(1);
    expect(router.register).toHaveBeenCalledWith(el);
  });

  it('should deregister itself from the router', async () => {
    const el = await fixture('<t-router></t-router>');
    el.parentElement.removeChild(el);

    expect(router.register).toHaveBeenCalledTimes(1);
    expect(router.deregister).toHaveBeenCalledTimes(1);
    expect(router.deregister).toHaveBeenCalledWith(el);
  });

  it('should render the current path', async () => {
    history.pushState({}, '', '/foo');
    const el = await fixture(
      '<t-router><t-test route="/foo"></t-test><t-test route="/bar"></t-test></t-router>'
    );

    expect(el.shadowRoot.innerHTML).toMatchSnapshot();
  });

  it('should replace the rendered node', async () => {
    history.pushState({}, '', '/foo');
    const el = await fixture(
      '<t-router><t-test route="/foo"></t-test><t-test route="/bar"></t-test></t-router>'
    );

    expect(el.shadowRoot.innerHTML).toMatchSnapshot();

    history.pushState({}, '', '/bar');
    el.navigationStateChanged();

    expect(el.shadowRoot.innerHTML).toMatchSnapshot();
  });

  it('should not rerender if not necessary', async () => {
    history.pushState({}, '', '/foo');
    const el = await fixture(
      '<t-router><t-test route="/foo"></t-test><t-test route="/bar"></t-test></t-router>'
    );

    expect(el.navigationStateChanged()).toBeFalsy();
  });

  it('should redirect to the default route if not found', async () => {
    history.pushState({}, '', '/bar');
    const el = await fixture(
      '<t-router default-route="/foo"><t-test route="/foo"></t-test></t-router>'
    );

    expect(router.redirect).toHaveBeenCalledWith('/foo');
  });

  it('should render a secured node if authenticated', async () => {
     authState.isAuthenticated.mockImplementationOnce(() => true);

    history.pushState({}, '', '/foo');
    const el = await fixture(
      '<t-router><t-test route="/foo" secured="true"></t-test></t-router>'
    );

    expect(el.shadowRoot.innerHTML).toMatchSnapshot();
  });

  it('should only redirect once if default route is secured', async () => {
    history.pushState({}, '', '/foo');
    const el = await fixture(
      `<t-router default-route="/bar" default-route-unsecure="/baz">
         <t-test route="/foo" secured></t-test>
         <t-test route="/bar" secured></t-test>
         <t-test route="/baz"></t-test>
       </t-router>`
    );

    expect(router.redirect).toHaveBeenCalledTimes(1);
    expect(router.redirect).toHaveBeenCalledWith('/baz');
  });
});
