const template = document.createElement('template');
template.innerHTML = `
<style>
  :host {
    contain: content;
    color: white;
  }
  
  .userInfo {
    display: inline-block;
    padding: 0 20px;
  }
</style>
<div class='userInfo'>
  <span>John Doe</span>
</div>
<t-button class='dark'>Logout</t-button>
`;

export default class UserInfo extends HTMLElement {
  constructor() {
    super();

    this.attachShadow({ mode: 'open' });
  }

  connectedCallback() {
    this.render();
  }

  render() {
    this.shadowRoot.appendChild(template.content.cloneNode(true));
  }
}
