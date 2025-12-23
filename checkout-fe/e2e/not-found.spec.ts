import { test, expect } from '@playwright/test';

test.describe('404 not found', () => {
  test('shows 404 page for unknown checkout id', async ({ page }) => {
    await page.goto('/checkout/non-existing-id-123');

    await expect(page.getByText('Error 404')).toBeVisible();

    await expect(page.getByRole('button', { name: /go back/i })).toBeVisible();
  });
});
