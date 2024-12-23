import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';

import { RouterProvider } from 'react-router-dom';
import { Provider } from 'react-redux';
import { NextUIProvider } from '@nextui-org/system';

import store from '@store/store';
import router from './routes/routes.jsx';

import App from './App.jsx';
import './index.css';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Provider store={store}>
      <RouterProvider router={router}>
        <NextUIProvider>
          <App />
        </NextUIProvider>
      </RouterProvider>
    </Provider>
  </StrictMode>
);
