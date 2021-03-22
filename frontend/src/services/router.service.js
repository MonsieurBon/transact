export default function Router() {
  const components = [];

  function callComponents() {
    components.find(c => c.navigationStateChanged());
  }

  this.go = url => {
    history.pushState({}, '', url);
    callComponents();
  };

  this.redirect = url => {
    history.replaceState({}, '', url);
    callComponents();
  };

  this.register = component => {
    components.push(component);
  };

  this.deregister = component => {
    components.splice(components.indexOf(component), 1);
  }
}
