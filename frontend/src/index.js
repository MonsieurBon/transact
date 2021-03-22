import App from './components/app.component.js';
import Button from './components/button.component.js';
import RouterLink from './components/router-link.component.js';
import Router from './components/router.component.js';
import UserInfo from './components/user-info.component.js';
import RouterService from './services/router.service.js';

const router = new RouterService();

window.customElements.define('router-link', RouterLink(router), { extends: 'a' });
window.customElements.define('t-app', App);
window.customElements.define('t-button', Button);
window.customElements.define('t-router', Router(router));
window.customElements.define('user-info', UserInfo);
