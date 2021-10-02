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
`;

export default function Router(authState, router) {
  return class Router extends HTMLElement {
    constructor() {
      super();

      this.routes = [];

      this.attachShadow({ mode: 'open' });
      this.shadowRoot.append(template.content.cloneNode(true));

      while (this.firstChild) {
        if (this.firstChild.hasAttribute && this.firstChild.hasAttribute('route')) {
          this.routes.push(this.firstChild);
        }
        this.removeChild(this.firstChild);
      }
      this.routerOutlet = this.shadowRoot.querySelector('#router-outlet');

      const defaultRouteAttr = this.getAttribute('default-route');
      const defaultRoute = this.routes.find(r => r.getAttribute('route') === defaultRouteAttr);
      this.defaultRouteIsSecured = !!defaultRoute && defaultRoute.hasAttribute('secured');
    }

    // noinspection JSUnusedGlobalSymbols
    connectedCallback() {
      router.register(this);

      this.render();
    }

    render() {
      const nextRoute = location.pathname;

      if (nextRoute === this.currentRoute) {
        return false;
      }

      this.clearRenderedNodes();

      if (this.routes.length === 0) {
        return false;
      }

      let route = this.routes.find(r => r.getAttribute('route') === nextRoute);

      if (!route) {
        this.redirectToDefaultRoute();
        return true
      } else {
        if (route.hasAttribute('secured') && !authState.isAuthenticated()) {
          this.redirectToDefaultRoute();
          return true;
        } else {
          this.routerOutlet.append(route);
          this.currentRoute = nextRoute;
          return true;
        }
      }

      return false;
    }

    redirectToDefaultRoute() {
      const defaultRouteAttr =
        this.defaultRouteIsSecured && !authState.isAuthenticated()
          ? 'default-route-unsecure'
          : 'default-route';
      const defaultRoute = this.getAttribute(defaultRouteAttr);
      router.redirect(defaultRoute);
    }

    clearRenderedNodes() {
      this.routerOutlet.childNodes.forEach(child => child.remove());
    }

    // noinspection JSUnusedGlobalSymbols
    navigationStateChanged() {
      return this.render();
    }

    // noinspection JSUnusedGlobalSymbols
    disconnectedCallback() {
      router.deregister(this);
    }
  };
}
