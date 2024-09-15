import { lazy } from 'react';

// project import
import Loadable from 'components/Loadable';
import MainLayout from 'layout/MainLayout';

// render - dashboard

// render - sample page
const AlbumsPage = Loadable(lazy(() => import('pages/albums/albums')));
const AboutPage = Loadable(lazy(() => import('pages/staticPages/about')));
const AlbumAddPage = Loadable(lazy(() => import('pages/albums/albumAdd')));
const AlbumShowPage = Loadable(lazy(() => import('pages/albums/albumShow')));
const AlbumUploadPage = Loadable(lazy(() => import('pages/albums/albumUpload')));
const AlbumEditPage = Loadable(lazy(() => import('pages/albums/albumEdit')));
const PhotoEditPage = Loadable(lazy(() => import('pages/albums/photoEdit')));

// render - utilities

// ==============================|| MAIN ROUTING ||============================== //

const MainRoutes = {
  path: '/',
  element: <MainLayout />,
  children: [
    {
      path: '/',
      element: <AlbumsPage />
    },
    {
      path: '/album/add',
      element: <AlbumAddPage />
    },
    {
      path: '/album/show',
      element: <AlbumShowPage />
    },
    {
      path: '/album/edit',
      element: <AlbumEditPage />
    },
    {
      path: '/album/upload',
      element: <AlbumUploadPage />
    },
    {
      path: '/photo/edit',
      element: <PhotoEditPage />
    },
    {
      path: '/about',
      element: <AboutPage />
    }
  ]
};

export default MainRoutes;
