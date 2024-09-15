import React, { useEffect, useState } from 'react';
import { Card, CardContent, Grid } from '@mui/material';
import { makeStyles } from '@mui/styles';
import { useNavigate, Link } from 'react-router-dom';
import { fetchGetDataWithAuth } from '../../client/client';

const brightPopColors = [
  '#FF5733',
  '#33FF57',
  '#3357FF',
  '#FF33A1',
  '#33FFF7',
  '#FFCC00',
  '#FF007F',
  '#00FF00',
  '#FF3300',
  '#7DFF33',
  '#33FF5E',
  '#FF6600',
  '#FF1493',
  '#00BFFF',
  '#8A2BE2',
  '#FF6347',
  '#7FFF00',
  '#00FFFF',
  '#FF00FF',
  '#FFD700',
  '#FF4500',
  '#ADFF2F',
  '#FF69B4',
  '#00FF7F',
  '#FF8C00',
  '#4B0082',
  '#8B00FF',
  '#DC143C',
  '#1E90FF',
  '#DAA520',
  '#32CD32',
  '#E6E6FA',
  '#D2691E',
  '#00FA9A',
  '#FF77FF',
  '#FFB6C1',
  '#2E8B57',
  '#F0E68C',
  '#B0E0E6',
  '#FF7F50'
];

const getRandomColor = () => {
  const randomIndex = Math.floor(Math.random() * brightPopColors.length);
  return brightPopColors[randomIndex];
};

const useStyles = makeStyles((theme) => ({
  card: {
    textAlign: 'center',
    padding: theme.spacing(3),
    borderRadius: theme.spacing(2),
    height: '250px',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    color: 'white' // Ensure the text is readable against any background color
  }
}));

const AlbumDynamicGridPage = () => {
  const [dataArray, setDataArray] = useState([]);
  const navigate = useNavigate();
  const classes = useStyles();

  useEffect(() => {
    const isLoggedIn = localStorage.getItem('token');
    if (!isLoggedIn) {
      navigate('/login');
      window.location.reload();
      return;
    }

    fetchGetDataWithAuth('/albums').then((res) => {
      const fetchedData = res.data;
      setDataArray(fetchedData);
    });
  }, [navigate]); // Only depend on 'navigate'

  return (
    <Grid container spacing={2}>
      {dataArray.map((data, index) => (
        <Grid item key={index} xs={12} sm={6} md={4} lg={3}>
          <Link to={`/album/show?id=${data.id}`}>
            <Card className={classes.card} style={{ backgroundColor: getRandomColor() }}>
              <CardContent>
                <h1 style={{ fontSize: '2rem', margin: 0, color: 'white' }}>{data.title}</h1>
              </CardContent>
            </Card>
          </Link>
        </Grid>
      ))}
    </Grid>
  );
};

export default AlbumDynamicGridPage;
