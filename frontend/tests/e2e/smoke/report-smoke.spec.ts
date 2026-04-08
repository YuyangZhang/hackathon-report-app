import { expect, test } from '@playwright/test';

test.describe('report app smoke', () => {
  test('redirects anonymous users to login and allows maker login', async ({ page }) => {
    await page.goto('/maker');

    await expect(page.getByRole('heading', { name: '报表管理系统登录' })).toBeVisible();

    await page.getByLabel('用户名：').fill('maker1');
    await page.getByLabel('密码：').fill('123456');
    await page.getByRole('button', { name: '登录' }).click();

    await expect(page).toHaveURL(/\/maker$/);
    await expect(page.getByRole('heading', { name: '报表管理系统' })).toBeVisible();
    await expect(page.getByText('当前用户：maker1')).toBeVisible();
  });
});
