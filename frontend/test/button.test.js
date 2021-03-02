import { fixture } from '@open-wc/testing-helpers';
import Button from '../src/components/button.js';

describe('Button', () => {
  beforeAll(() => {
    window.customElements.define('t-button', Button);
  });

  it('should render', async () => {
    const el = await fixture('<t-button>My button</t-button>');
    expect(el.shadowRoot.innerHTML).toMatchSnapshot();
  });
});
