const template = document.createElement('template');
template.innerHTML = `
<style>
  :host {
    contain: content;
  }
  
  button {
    background: transparent;
    border: 1px solid black;
    border-radius: 5px;
    height: 35px;
    padding: 0 20px;
  }
  
  :host(.dark) button {
    background: black;
    border-color: white;
    color: white;
  }
  
  :host(.light) button {
    background: white;
  }
</style>
<button><slot /></button>
`;

export default class Button extends HTMLElement {
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
