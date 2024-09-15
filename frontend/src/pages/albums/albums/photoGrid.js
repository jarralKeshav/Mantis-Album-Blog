import React, { useEffect, useState } from 'react';
import { Button, Card, CardContent, CardMedia, Grid, Tooltip, Typography } from '@mui/material';
import {
  fetchGetDataWithAuth,
  fetchGetDataWithAuthArrayBuffer,
  fetchDeleteDataWithAuth,
  fetchGetBlobDataWithAuth
} from '../../../client/client';
import { useLocation } from 'react-router-dom';
import { Buffer } from 'buffer';
import { makeStyles } from '@mui/styles';
import Modal from '@mui/material/Modal';

const useStyles = makeStyles((theme) => ({
  modal: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  },
  modalMain: {
    backgroundColor: theme.palette.background.paper,
    borderRadius: '10px',
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3),
    maxHeight: '75%',
    maxWidth: '75%',
    overflow: 'auto',
    display: 'flex',
    flexDirection: 'column' // Add this to stack content vertically
  },
  closeButton: {
    marginLeft: 'auto'
  },
  buttonContainer: {
    marginTop: 'auto', // Push buttons to the bottom
    display: 'flex',
    justifyContent: 'space-between' // Ensure buttons are spaced evenly
  }
}));

const PhotoGrid = () => {
  const [photos, setPhotos] = useState({});
  const [albumInfo, setAlbumInfo] = useState({});
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const album_id = queryParams.get('id');
  const [open, setOpen] = useState(false);
  const classes = useStyles();
  const [photoContent, setPhotoContent] = useState(null);
  const [photoDesc, setPhotoDesc] = useState('');
  const [downloadLink, setDownloadLink] = useState(null);

  const handleOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };

  const handleView = (download_link, description) => {
    fetchGetDataWithAuthArrayBuffer(download_link).then((response) => {
      const buffer = Buffer.from(response.data, 'binary').toString('base64');
      setPhotoContent(buffer);
    });
    setDownloadLink(download_link);
    setPhotoDesc(description);
    handleOpen();
  };
  const handleDownload = (download_link) => {
    console.log(download_link);
    fetchGetBlobDataWithAuth(download_link)
      .then((response) => {
        console.log(response);

        const disposition = response.headers.get('content-disposition');
        const match = /filename="(.*)"/.exec(disposition);
        const filename = match ? match[1] : 'downloadedFile';
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
      })
      .catch((error) => {
        console.error('Error downloading page ', error);
      });
  };
  const handleDelete = (photo_id) => {
    const isConfirmed = window.confirm('Are you sure you want to delete the Photo?');
    console.log(isConfirmed);
    if (isConfirmed) {
      fetchDeleteDataWithAuth('/albums/' + album_id + '/photos/' + photo_id + '/delete').then((res) => {
        console.log(res);
        window.location.reload();
      });
    } else {
      console.log('Delete operation failed!!');
    }
  };

  useEffect(() => {
    fetchGetDataWithAuth(`/albums/${album_id}`).then((res) => {
      setAlbumInfo(res.data);
      console.log('response: ', res.data.photos);
      const photoList = res.data.photos;
      photoList.forEach((photo) => {
        let thumbnail_link = photo.download_link.replace('/download-photo', '/download-thumbnail');
        fetchGetDataWithAuthArrayBuffer(thumbnail_link).then((response) => {
          const albumPhotoId = 'album_' + album_id + '_photo+' + photo.id;
          const buffer = Buffer.from(response.data, 'binary').toString('base64');

          const temp = {
            album_id: album_id,
            photo_id: photo.id,
            name: photo.title,
            description: photo.description,
            content: buffer,
            download_link: photo.download_link
          };

          setPhotos((prevPhotos) => ({ ...prevPhotos, [albumPhotoId]: temp }));
        });
      });
    });
  }, []);

  return (
    <div>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby={'modal-modal-title'}
        aria-describedby={'modal-modal-description'}
        className={classes.modal}
      >
        <div className={classes.modalMain}>
          <img src={'data:image/jpeg;base64,' + photoContent} alt={photoDesc} style={{ width: '100%', height: 'auto' }} />
          <div className={classes.buttonContainer}>
            <Button onClick={() => handleDownload(downloadLink)}>Download Photo</Button>
            <Button onClick={handleClose} className={classes.closeButton}>
              Close
            </Button>
          </div>
        </div>
      </Modal>
      <Typography variant={'h4'} gutterBottom>
        {albumInfo.title}
      </Typography>
      <Typography variant={'subtitle1'} gutterBottom>
        {albumInfo.description}
      </Typography>
      <Grid container spacing={2}>
        {Object.keys(photos).map((key) => (
          <Grid item key={key} xs={8} sm={4} md={4} lg={2}>
            <Card>
              <Tooltip title={photos[key]['descritpion']}>
                <CardMedia
                  component="img"
                  height="200"
                  image={'data:image/jpeg;base64,' + photos[key]['content']}
                  alt={photos[key]['description']}
                  // title={photos[key]['description']}
                />
              </Tooltip>

              <CardContent>
                <Tooltip title={photos[key]['description']}>
                  <Typography variant="subtitle1">{photos[key]['name']}</Typography>
                </Tooltip>
                <a href="#" onClick={() => handleView(photos[key]['download_link'], photos[key]['description'])}>
                  View
                </a>{' '}
                |{' '}
                <a
                  href={`/photo/edit?album_id=${album_id}&photo_id=${photos[key]['photo_id']}&photo_name=${photos[key]['name']}&photo_desc=${photos[key]['description']}`}
                >
                  Edit
                </a>{' '}
                |{' '}
                <a href="#" onClick={() => handleDownload(photos[key]['download_link'])}>
                  Download
                </a>{' '}
                |{' '}
                <a href="#" onClick={() => handleDelete(photos[key]['photo_id'])}>
                  Delete
                </a>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </div>
  );
};

export default PhotoGrid;
