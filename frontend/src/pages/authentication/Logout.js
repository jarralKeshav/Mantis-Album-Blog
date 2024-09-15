const Logout = () => {
  localStorage.removeItem('token');
  window.location.href = '/login';
};

export default Logout;
