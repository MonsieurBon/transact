/**
 * @jest-environment jsdom
 */

import Router from '../../src/services/router.service.js';

describe('Router service', () => {
  beforeEach(() => {
    history.pushState = jest.fn();
    history.replaceState = jest.fn();
    history.back = jest.fn(() => window.dispatchEvent(new Event('popstate')));
  });

  it('should be created', () => {
    const router = new Router();
    expect(router).toBeDefined();
  });

  it('should add to history if go', () => {
    const router = new Router();
    router.go('/foo');
    expect(history.pushState).toHaveBeenCalledWith({}, '', '/foo');
  });

  it('should replace history if redirect', () => {
    const router = new Router();
    router.redirect('/bar');
    expect(history.replaceState).toHaveBeenCalledWith({}, '', '/bar');
  });

  it('should call registered components on go', () => {
    const component = {
      navigationStateChanged: jest.fn(),
    };

    const router = new Router();
    router.register(component);
    router.go('/foo');

    expect(component.navigationStateChanged).toHaveBeenCalledTimes(1);
  });

  it('should call registered components on redirect', () => {
    const component = {
      navigationStateChanged: jest.fn(),
    };

    const router = new Router();
    router.register(component);
    router.redirect('/bar');

    expect(component.navigationStateChanged).toHaveBeenCalledTimes(1);
  });

  it('should stop calling components if event was handled', () => {
    const c1 = {
      navigationStateChanged: jest.fn(() => true),
    };

    const c2 = {
      navigationStateChanged: jest.fn(() => false),
    }

    const router = new Router();
    router.register(c1);
    router.register(c2);
    router.go('/foo');

    expect(c1.navigationStateChanged).toHaveBeenCalledTimes(1);
    expect(c2.navigationStateChanged).toHaveBeenCalledTimes(0);
  });

  it('should continue calling until handled', () => {
    const c1 = {
      navigationStateChanged: jest.fn(),
    };

    const c2 = {
      navigationStateChanged: jest.fn(),
    }

    const router = new Router();
    router.register(c1);
    router.register(c2);
    router.go('/foo');

    expect(c1.navigationStateChanged).toHaveBeenCalledTimes(1);
    expect(c2.navigationStateChanged).toHaveBeenCalledTimes(1);
  });

  it('should not call deregistered components', () => {
    const c1 = {
      navigationStateChanged: jest.fn(),
    }

    const c2 = {
      navigationStateChanged: jest.fn(),
    }

    const router = new Router();
    router.register(c1);
    router.register(c2);
    router.deregister(c1);
    router.go('/foo');

    expect(c1.navigationStateChanged).toHaveBeenCalledTimes(0);
    expect(c2.navigationStateChanged).toHaveBeenCalledTimes(1);
  });

  it('should call registered components on back button click', () => {
    const component = {
      navigationStateChanged: jest.fn(),
    };

    const router = new Router();
    router.register(component);

    history.back();

    expect(component.navigationStateChanged).toHaveBeenCalledTimes(1);
  })
});
