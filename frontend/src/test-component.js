const template = document.createElement('template');
template.innerHTML = `
<style>
  :host {
    contain: content;
  }
</style>
<h1>It worked!</h1>
`;

export default class TestComponent extends HTMLElement {
  constructor(service) {
    super();

    this.service = service;

    this.attachShadow({ mode: 'open' });

    this.service.logToConsole('constructor');
  }

  connectedCallback() {
    this.render();
    this.service.logToConsole('connectedCallback');
  }

  render() {
    this.shadowRoot.appendChild(template.content.cloneNode(true));
  }
}
