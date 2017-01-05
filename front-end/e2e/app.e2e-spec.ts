import { MyNg2Page } from './app.po';

describe('my-ng2 App', function() {
  let page: MyNg2Page;

  beforeEach(() => {
    page = new MyNg2Page();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
