import { ProLayoutProps } from '@ant-design/pro-components';

/**
 * @name
 */
const Settings: ProLayoutProps & {
  pwa?: boolean;
  logo?: string;
  navTheme?: string
} = {
  navTheme: 'light',
  colorPrimary: "#1677FF",
  layout: 'top',
  contentWidth: 'Fixed',
  fixedHeader: false,
 // fixSiderbar: true,
  colorWeak: false,
  splitMenus: false,
  title: '接口开放平台',
  pwa: false,
  // logo: 'https://img.qimuu.icu/typory/logo.gif',
  iconfontUrl: 'https://pic.imgdb.cn/item/65e0254b9f345e8d036d1f3e.jpg',
};
export default Settings;
