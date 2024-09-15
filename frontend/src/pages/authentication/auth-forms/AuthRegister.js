import { Button, Container, TextField } from '@mui/material';
import { useEffect, useState } from 'react';
import { fetchPostData } from '../../../client/client';
import { useNavigate } from 'react-router-dom';

const AuthRegister = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState({ email: '', password: '' });
  const [registerError, setRegisterError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const isLoggedIn = localStorage.getItem('token');
    if (isLoggedIn) {
      navigate('/');
      window.location.reload();
    }
  }, [navigate]);

  const validateEmail = () => {
    const emailRegex = /[a-z0-9]+@[a-z]+\.[a-z]{2,3}/;
    return emailRegex.test(email);
  };

  const validatePassword = () => {
    return password.length >= 6 && password.length <= 16;
  };

  const handleLogin = async () => {
    setErrors({ email: '', password: '' });

    if (!validateEmail()) {
      setErrors((prevErrors) => ({ ...prevErrors, email: 'Invalid email format' }));
      return;
    }

    if (!validatePassword()) {
      setErrors((prevErrors) => ({ ...prevErrors, password: 'Password must be at least 6 characters' }));
      return;
    }

    fetchPostData('/auth/users/add', { email, password })
      .then(() => {
        // const { token } = response.data;
        setRegisterError('');
        // localStorage.setItem('token', token);
        navigate('/login');
        window.location.reload();
      })
      .catch((error) => {
        console.error('Login Error: ', error);
        setRegisterError('An error occurred during registration');
      });
  };

  return (
    <Container component="main" maxWidth="xs">
      <TextField
        variant="outlined"
        margin={'normal'}
        fullWidth
        label="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        error={!!errors.email}
        helperText={errors.email}
      />

      <TextField
        variant="outlined"
        margin={'normal'}
        fullWidth
        label={'Password'}
        type={'password'}
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        error={!!errors.password}
        helperText={errors.password}
      />

      <Button variant="contained" color="primary" fullWidth onClick={handleLogin}>
        Register
      </Button>
      {registerError && <p style={{ color: 'red' }}>{registerError}</p>}
    </Container>
  );
};

export default AuthRegister;
