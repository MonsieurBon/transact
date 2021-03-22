import {
  fixture,
  fixtureCleanup,
} from '@open-wc/testing-helpers/index-no-side-effects.js';
import Router from '../../src/components/router.component.js';

describe('Router component', () => {
  let router;

  beforeAll(() => {
    router = {
      register: jest.fn(),
      deregister: jest.fn(),
    };
    window.customElements.define('t-router', Router(router));
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
      '<t-router><div data-route="/foo"></div><div data-route="/bar"></div></t-router>'
    );

    expect(el.shadowRoot.innerHTML).toMatchSnapshot();
  });

  it('should replace the rendered node', async () => {
    history.pushState({}, '', '/foo');
    const el = await fixture(
      '<t-router><div data-route="/foo"></div><div data-route="/bar"></div></t-router>'
    );

    expect(el.shadowRoot.innerHTML).toMatchSnapshot();

    history.pushState({}, '', '/bar');
    el.navigationStateChanged();

    expect(el.shadowRoot.innerHTML).toMatchSnapshot();
  });

  it('should not rerender if not necessary', async () => {
    history.pushState({}, '', '/foo');
    const el = await fixture(
      '<t-router><div data-route="/foo"></div><div data-route="/bar"></div></t-router>'
    );

    expect(el.navigationStateChanged()).toBeFalsy();
  });
});
