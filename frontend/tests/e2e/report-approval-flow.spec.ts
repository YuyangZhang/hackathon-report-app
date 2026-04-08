import { expect, test } from '@playwright/test';

test.describe('maker checker approval flow', () => {
  test('maker submits report and checker approves it with audit trail visibility', async ({ browser }) => {
    const makerContext = await browser.newContext({ acceptDownloads: true });
    const checkerContext = await browser.newContext({ acceptDownloads: true });
    const makerPage = await makerContext.newPage();
    const checkerPage = await checkerContext.newPage();

    try {
      await makerPage.goto('/login');
      await makerPage.getByLabel('用户名：').fill('maker1');
      await makerPage.getByLabel('密码：').fill('123456');
      await makerPage.getByRole('button', { name: '登录' }).click();

      await expect(makerPage).toHaveURL(/\/maker$/);
      await makerPage.locator('select').first().selectOption({ index: 1 });
      await makerPage.getByRole('button', { name: '执行报表' }).click();
      await expect(makerPage.getByText('查询结果')).toBeVisible();
      await expect(makerPage.getByRole('heading', { name: '当前报表运行状态' })).toBeVisible();

      await makerPage.getByRole('button', { name: '提交审批' }).click();
      await expect(makerPage.getByText('已提交审批')).toBeVisible();

      await checkerPage.goto('/login');
      await checkerPage.getByLabel('用户名：').fill('checker1');
      await checkerPage.getByLabel('密码：').fill('123456');
      await checkerPage.getByRole('button', { name: '登录' }).click();

      await expect(checkerPage).toHaveURL(/\/checker$/);
      await expect(checkerPage.getByRole('heading', { name: '待审批报表' })).toBeVisible();
      await expect(checkerPage.getByRole('heading', { name: '审批详情' })).toBeVisible();
      await expect(checkerPage.getByText('Maker：maker1')).toBeVisible();

      await checkerPage.locator('select').first().selectOption({ index: 0 });
      await checkerPage.locator('textarea').fill('approved by e2e');
      await checkerPage.getByRole('button', { name: '提交审批结果' }).click();
      await expect(checkerPage.getByRole('button', { name: '刷新' }).nth(1)).toBeVisible();

      await checkerPage.getByRole('button', { name: '刷新' }).nth(1).click();
      await expect(checkerPage.getByRole('heading', { name: '历史审批记录' })).toBeVisible();
      await expect(checkerPage.getByText('Approved')).toBeVisible();
      await checkerPage.getByRole('button', { name: '查看流程' }).first().click();
      await expect(checkerPage).toHaveURL(/\/runs\/\d+\/flow$/);
      await expect(checkerPage.getByText('Approved')).toBeVisible();

      await makerPage.reload();
      await expect(makerPage.getByRole('heading', { name: '我的提交历史' })).toBeVisible();
      await expect(makerPage.getByText('Approved')).toBeVisible();
      const downloadPromise = makerPage.waitForEvent('download');
      await makerPage.getByRole('button', { name: '下载 Excel' }).first().click();
      const download = await downloadPromise;
      expect(download.suggestedFilename()).toContain('.xlsx');
    } finally {
      await makerContext.close();
      await checkerContext.close();
    }
  });
});
