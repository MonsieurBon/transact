const template = document.createElement('template');
template.innerHTML = `
<style>
  :host {
    display: flex;
    contain: content;
    height: 100%;
    width: 100%;
    flex-direction: column;
  }
  
  .header {
    background: black;
    height: 100px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
  }
  
  .header h1 {
    color: white;
  }
  
  .main {
    flex-grow: 1;
    display: flex;
    flex-direction: row;
  }
  
  .side {
    background: lightgray;
    width: 200px;
    border-right: 2px solid gray;
    box-shadow: 2px 3px 4px 1px darkgray;
  }
  
  .content {
    flex-grow: 1;
  }
</style>

<div class='header'>
  <h1>Transact</h1>
  <user-info></user-info>
</div>
<div class='main'>
  <div class='side'></div>
  <div class='content'></div>
</div>
`;

export default class App extends HTMLElement {
  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
  }

  // noinspection JSUnusedGlobalSymbols
  connectedCallback() {
    this.render();
  }

  render() {
    this.shadowRoot.append(template.content.cloneNode(true));
  }
}
