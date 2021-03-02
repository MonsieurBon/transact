import { fixture } from '@open-wc/testing-helpers';
import App from '../src/components/app';

describe('App', () => {
  beforeAll(() => {
    window.customElements.define('t-app', App);
  });

  it('should render', async () => {
    const el = await fixture('<t-app></t-app>');
    expect(el.shadowRoot.innerHTML).toMatchSnapshot();
  });
});
