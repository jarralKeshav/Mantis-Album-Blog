import React from 'react';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';
import { fetchDeleteDataWithAuth } from '../../../client/client';

const Header = () => {
  const query = new URLSearchParams(useLocation().search);
  const id = query.get('id');

  const handleDelete = () => {
    const isConfirmed = window.confirm('Are you sure you want to delete the album?');
    console.log(isConfirmed);

    if (isConfirmed) {
      fetchDeleteDataWithAuth('/albums/' + id + '/delete').then((res) => {
        console.log(res);
        window.location.href = '/';
        alert('Album deleted');
      });
    } else {
      console.log('Delete operation cancelled');
    }
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Photo Gallery
        </Typography>
        <Button
          component={Link}
          to={`/album/edit?id=${id}`}
          color="inherit"
          variant="contained"
          sx={{ mr: 2, bgcolor: '#d933ff', '&:hover': { bgcolor: 'primary.dark' } }}
        >
          Edit Album
        </Button>
        <Button
          component={Link}
          to={`/album/upload?id=${id}`}
          color="inherit"
          variant="contained"
          sx={{ mr: 2, bgcolor: '#00ff00', '&:hover': { bgcolor: 'primary.dark' } }}
        >
          Upload Photo
        </Button>
        <Button
          onClick={handleDelete}
          color="inherit"
          variant="contained"
          sx={{ mr: 2, bgcolor: '#FF0000', '&:hover': { bgcolor: 'primary.dark' } }}
        >
          Delete Album
        </Button>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
