const template = document.createElement('template');
template.innerHTML = `
<style>
  :host {
    contain: content;
  }
  
  slot {
    display: none;
  }
</style>
<div id='router-outlet'></div>
<slot />
`;

export default function Router(router) {
  return class extends HTMLElement {
    constructor() {
      super();

      this.routes = [];

      this.attachShadow({ mode: 'open' });
      this.shadowRoot.append(template.content.cloneNode(true));
    }

    connectedCallback() {
      router.register(this);

      let slotNode = this.shadowRoot.querySelector('slot');
      this.routerOutlet = this.shadowRoot.querySelector('#router-outlet')
      this.routes = slotNode.assignedElements();

      this.render();
    }

    render() {
      const nextRoute = location.pathname;

      if (nextRoute === this.currentRoute) {
        return false;
      }

      this.clearRenderedNodes();
      const route = this.routes.find(r => r.getAttribute('data-route') === nextRoute);
      if (route) {
        this.routerOutlet.append(route);
        this.currentRoute = nextRoute;
        return true;
      }
    }

    clearRenderedNodes() {
      this.routerOutlet.childNodes.forEach(child => child.remove());
    }

    navigationStateChanged() {
      return this.render();
    }

    disconnectedCallback() {
      router.deregister(this);
    }
  }
}
