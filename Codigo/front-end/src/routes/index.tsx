import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from '../pages/LoginPage';
import { SignUpPage } from '../pages/SignUpPage';
import { HomePage } from '../pages/HomePage';
import { ProjectLayout } from '../pages/ProjectLayout';
import { ProjectPage } from '../pages/ProjectPage';
import { GCEEditorPage } from '../pages/GCEEditorPage';
import { DecisionTablePage } from '../pages/DecisionTablePage';
import { GFCViewerPage } from '../pages/GFCViewerPage';
import { useAuth } from '../context/AuthContext';
import type { ReactNode } from 'react';

function ProtectedRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
}

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/cadastro" element={<SignUpPage />} />
        <Route path="/homepage" element={<ProtectedRoute><HomePage /></ProtectedRoute>} />
        <Route path="/projeto/:projectId" element={<ProtectedRoute><ProjectLayout /></ProtectedRoute>}>
          <Route index element={<ProjectPage />} />
          <Route path="gce/:gceId" element={<GCEEditorPage />} />
          <Route path="gce/:gceId/tabela-decisao" element={<DecisionTablePage />} />
          <Route path="gfc/:gfcId" element={<GFCViewerPage />} />
        </Route>
        <Route path="/" element={<Navigate to="/homepage" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export { ProtectedRoute };