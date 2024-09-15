// assets
import { LoginOutlined, LogoutOutlined, ProfileOutlined } from '@ant-design/icons';

// icons
const icons = {
  LoginOutlined,
  ProfileOutlined,
  LogoutOutlined
};

const isLoginEnabled = localStorage.getItem('token');

const caseLogin = [
  {
    id: 'logout1',
    title: 'Logout',
    type: 'item',
    url: '/logout',
    icon: icons.LogoutOutlined,
    target: true
  }
];

const caseLogout = [
  {
    id: 'Login',
    title: 'Login',
    type: 'item',
    url: '/login',
    icon: icons.LoginOutlined,
    target: true
  },
  {
    id: 'register1',
    title: 'Register',
    type: 'item',
    url: '/register',
    icon: icons.ProfileOutlined,
    target: true
  }
];

const auth = {
  id: 'authentication',
  title: 'Authentication',
  type: 'group',
  children: [isLoginEnabled && caseLogin[0], !isLoginEnabled && caseLogout[0], !isLoginEnabled && caseLogout[1]].filter(Boolean)
};

export default auth;
