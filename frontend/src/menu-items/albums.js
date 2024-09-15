// assets
import { ChromeOutlined, PictureOutlined, FileImageOutlined } from '@ant-design/icons';

// icons
const icons = {
  ChromeOutlined,
  PictureOutlined,
  FileImageOutlined
};

// ==============================|| MENU ITEMS - SAMPLE PAGE & DOCUMENTATION ||============================== //

const albums = {
  id: 'albums',
  title: 'Albums',
  type: 'group',
  children: [
    {
      id: 'Album',
      title: 'Albums',
      type: 'item',
      url: '/',
      icon: icons.PictureOutlined
    },
    {
      id: 'AddAlbum',
      title: 'Add Albums',
      type: 'item',
      url: '/album/add',
      icon: icons.FileImageOutlined
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

export default albums;
