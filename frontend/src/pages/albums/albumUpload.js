import React, { useState } from 'react';
//import { useNavigate } from 'react-router-dom';
import { useDropzone } from 'react-dropzone';
import { makeStyles } from '@mui/styles';
import { Button, IconButton, Typography } from '@mui/material';
import { Delete } from '@mui/icons-material';
import { useLocation, useNavigate } from 'react-router-dom';
import { fetchUploadFileUploadWithAuth } from '../../client/client';

const useStyles = makeStyles((theme) => ({
  dropzone: {
    border: '2px dashed #cccccc',
    borderRadius: theme.spacing(2),
    padding: theme.spacing(4),
    textAlign: 'center',
    backgroundColor: '#fafafa',
    color: '#bdbdbd',
    transition: 'border .24s ease-in-out',
    cursor: 'pointer',
    '&:hover': {
      border: '2px dashed #aaaaaa'
    }
  },
  thumbsContainer: {
    display: 'flex',
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginTop: theme.spacing(2)
  },
  thumb: {
    display: 'inline-flex',
    borderRadius: 2,
    border: '1px solid #eaeaea',
    marginBottom: theme.spacing(2),
    marginRight: theme.spacing(2),
    width: 100,
    height: 100,
    padding: 4,
    boxSizing: 'border-box',
    position: 'relative'
  },
  thumbInner: {
    display: 'flex',
    minWidth: 0,
    overflow: 'hidden'
  },
  img: {
    display: 'block',
    width: 'auto',
    height: '100%'
  },
  removeButton: {
    position: 'absolute',
    top: 0,
    right: 0,
    color: 'red'
  },
  uploadButton: {
    marginTop: theme.spacing(2)
  }
}));

const PhotoUploadDropzone = () => {
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const id = queryParams.get('id');

  const navigate = useNavigate();
  const classes = useStyles();

  const onDrop = (acceptedFiles) => {
    setFiles((prevFiles) => [...prevFiles, ...acceptedFiles]);
  };

  const removeFile = (file) => {
    setFiles((prevFiles) => prevFiles.filter((f) => f !== file));
  };

  const handleUpload = async () => {
    try {
      const formData = new FormData();
      files.forEach((file) => {
        formData.append('files', file);
      });

      setUploading(true);
      const response = await fetchUploadFileUploadWithAuth('/albums/' + id + '/upload-photos', formData);
      console.log('Upload seccessful', response);
      navigate(`/album/show?id=${id}`);
      setFiles([]); // Clear files after successful upload
    } catch (error) {
      console.error('Upload failed:', error);
    } finally {
      setUploading(false);
    }
  };

  const thumbs = files.map((file) => (
    <div className={classes.thumb} key={file.name}>
      <div className={classes.thumbInner}>
        <img src={URL.createObjectURL(file)} className={classes.img} alt={file.name} />
        <IconButton size="small" className={classes.removeButton} onClick={() => removeFile(file)}>
          <Delete />
        </IconButton>
      </div>
    </div>
  ));

  const { getRootProps, getInputProps } = useDropzone({
    onDrop,
    accept: {
      'image/png': ['.png'],
      'image/jpg': ['.jpg'],
      'image/jpeg': ['.jpeg']
    },
    multiple: true
  });

  return (
    <div>
      <div {...getRootProps({ className: classes.dropzone })}>
        <input {...getInputProps()} />
        <Typography variant="h6">Drag & drop photos here, or click to select files</Typography>
        <em>(Only image files are accepted)</em>
      </div>
      <aside className={classes.thumbsContainer}>{thumbs}</aside>
      <Button
        variant="contained"
        color="primary"
        className={classes.uploadButton}
        onClick={handleUpload}
        disabled={files.length === 0 || uploading}
      >
        {uploading ? 'Uploading...' : 'Upload Photos'}
      </Button>
    </div>
  );
};

export default PhotoUploadDropzone;
