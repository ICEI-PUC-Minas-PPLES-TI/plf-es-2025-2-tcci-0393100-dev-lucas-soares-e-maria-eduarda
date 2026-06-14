import { AuthProvider } from './context/AuthContext';
import { AppRouter } from './routes';

function App() {
  return (
    <AuthProvider>
      <AppRouter />
    </AuthProvider>
  );
}

export default App;