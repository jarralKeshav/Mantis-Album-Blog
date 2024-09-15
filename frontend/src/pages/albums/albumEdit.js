import { Button, TextField } from '@mui/material';
import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { fetchGetDataWithAuth, fetchPutDataWithAuth } from '../../client/client';

const EditAlbumForm = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const album_id = queryParams.get('id');
  useEffect(() => {
    const isLoggedIn = localStorage.getItem('token');
    if (!isLoggedIn) {
      navigate('/login');
      window.location.reload();
    }
    fetchGetDataWithAuth('/albums/' + album_id).then((res) => {
      if (res.data) {
        setFormData((prevFormData) => ({
          ...prevFormData,
          title: res.data.title,
          description: res.data.description
        }));
      }
    });
  }, [navigate]);

  const [formData, setFormData] = useState({
    title: '',
    description: ''
  });

  const [errors, setErrors] = useState({
    title: '',
    description: ''
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    let isValid = true;
    const newErrors = { title: '', description: '' };

    if (!formData.title.trim()) {
      newErrors.title = 'Name is required';
      isValid = false;
    }

    if (!formData.description.trim()) {
      newErrors.description = 'Description is required';
      isValid = false;
    }

    setErrors(newErrors);

    if (isValid) {
      const payload = {
        title: formData.title,
        description: formData.description
      };

      fetchPutDataWithAuth('/albums/' + album_id + '/update', payload)
        .then((response) => {
          console.log(response);
        })
        .catch((error) => {
          console.error('Login error:', error);
        });

      console.log('Form submitted: ', payload);
      navigate('/');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <TextField
        fullWidth
        label="Title"
        variant="outlined"
        name="title"
        value={formData.title}
        onChange={handleInputChange}
        error={!!errors.title}
        helperText={errors.title}
        margin="normal"
      />
      <TextField
        fullWidth
        label="Description"
        variant="outlined"
        name="description"
        value={formData.description}
        onChange={handleInputChange}
        error={!!errors.description}
        helperText={errors.description}
        multiline
        rows={4}
        margin="normal"
      />
      <Button type="submit" variant="contained" color="primary">
        Update Album
      </Button>
    </form>
  );
};

export default EditAlbumForm;
