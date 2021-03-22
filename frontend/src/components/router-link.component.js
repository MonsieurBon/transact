export default function RouterLink(router) {
  return class extends HTMLAnchorElement {
    constructor() {
      super();
    }

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

    disconnectedCallback() {
      this.removeEventListener('click', this.navigate);
    }
  }
}
