// assets
import { QuestionOutlined } from '@ant-design/icons';

// icons
const icons = {
  // ChromeOutlined,
  QuestionOutlined
};

const pages = {
  id: 'pages',
  title: 'pages',
  type: 'group',
  children: [
    {
      id: 'About',
      title: 'About',
      type: 'item',
      url: '/about',
      icon: icons.QuestionOutlined
    }
    // {
    //   id: 'documentation',
    //   title: 'Documentation',
    //   type: 'item',
    //   url: 'https://codedthemes.gitbook.io/mantis/',
    //   icon: icons.QuestionOutlined,
    //   external: true,
    //   target: true
    // }
  ]
};

export default pages;
