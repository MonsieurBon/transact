export default function RouterLink(router) {
  return class RouterLink extends HTMLAnchorElement {
    constructor() {
      super();
    }

    // noinspection JSUnusedGlobalSymbols
    connectedCallback() {
      this.addEventListener('click', this.navigate);
    }

    navigate(e) {
      e.preventDefault();
      e.stopPropagation();
      let location = this.getAttribute("href");

      if (location) {
        router.go(location);
      }
    }

    // noinspection JSUnusedGlobalSymbols
    disconnectedCallback() {
      this.removeEventListener('click', this.navigate);
    }
  }
}
