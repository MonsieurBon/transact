import {
  fixture,
  fixtureCleanup,
} from '@open-wc/testing-helpers/index-no-side-effects.js';
import RouterLink from '../../../src/components/blocks/router-link.component.js';

describe('RouterLink', () => {
  let router;

  beforeAll(() => {
    router = {
      go: jest.fn(),
    };
    window.customElements.define('router-link', RouterLink(router), {
      extends: 'a',
    });
  });

  afterEach(() => {
    fixtureCleanup();
    jest.clearAllMocks();
  });

  it('should render', async () => {
    const el = await fixture('<a is="router-link" href="/foo">link text</a>');
    expect(el).toMatchSnapshot();
  });

  it('should call router service go on click', async () => {
    const el = await fixture('<a is="router-link" href="/foo">link text</a>');
    el.click();
    expect(router.go).toHaveBeenCalledWith('/foo');
  });

  it('should stop event propagation', async (done) => {
    window.addEventListener('click', () => {
      fail();
    });
    const el = await fixture('<a is="router-link">link text</a>');
    el.click();

    setTimeout(done);
  });

  it('should not fire without href', async () => {
    const el = await fixture('<a is="router-link">link text</a>');
    el.click();

    expect(router.go).not.toHaveBeenCalled();
  });
});
