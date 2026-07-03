import { test, expect } from '@playwright/test';

/**
 * Returns true when the URL targets a host other than localhost / 127.0.0.1.
 * `data:` and `blob:` URIs are considered internal and are always allowed.
 */
function isExternalUrl(url: string): boolean {
  if (url.startsWith('data:') || url.startsWith('blob:')) {
    return false;
  }
  try {
    const { hostname } = new URL(url);
    return hostname !== 'localhost' && hostname !== '127.0.0.1' && hostname !== '::1';
  } catch {
    // Malformed or relative URL — treat as internal.
    return false;
  }
}

test.describe('no external network requests', () => {
  test('404 page loads no external resources', async ({ page }) => {
    const externalRequests: string[] = [];

    page.on('request', (request) => {
      if (isExternalUrl(request.url())) {
        externalRequests.push(request.url());
      }
    });

    await page.goto('/checkout/non-existing-id-123');
    await page.waitForLoadState('load');

    expect(
      externalRequests,
      `External URLs were requested:\n${externalRequests.join('\n')}`,
    ).toHaveLength(0);
  });

  test('checkout page loads no external resources', async ({ page }) => {
    const externalRequests: string[] = [];

    page.on('request', (request) => {
      if (isExternalUrl(request.url())) {
        externalRequests.push(request.url());
      }
    });

    await page.goto('/checkout/test-checkout-id');
    await page.waitForLoadState('load');

    expect(
      externalRequests,
      `External URLs were requested:\n${externalRequests.join('\n')}`,
    ).toHaveLength(0);
  });
});
