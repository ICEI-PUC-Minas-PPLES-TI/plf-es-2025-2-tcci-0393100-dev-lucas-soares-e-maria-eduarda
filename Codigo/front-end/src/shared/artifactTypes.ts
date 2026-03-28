import type { ComponentType } from 'react';
import { FileCode, GitBranch, Network, Table } from 'lucide-react';

export type ArtifactType = 'GFC' | 'GCE' | 'TABLE' | 'CODE';

interface ArtifactTypeConfig {
  label: string;
  shortLabel: string;
  icon: ComponentType<{ className?: string }>;
  color: string;
  bgColor: string;
}

export const ARTIFACT_TYPES: Record<ArtifactType, ArtifactTypeConfig> = {
  GFC: {
    label: 'Grafo de Fluxo',
    shortLabel: 'GFC',
    icon: GitBranch,
    color: 'text-cyan-400',
    bgColor: 'bg-cyan-500/10',
  },
  GCE: {
    label: 'Grafo Causa-Efeito',
    shortLabel: 'GCE',
    icon: Network,
    color: 'text-green-400',
    bgColor: 'bg-green-500/10',
  },
  TABLE: {
    label: 'Tabela de Decisão',
    shortLabel: 'Tabela',
    icon: Table,
    color: 'text-yellow-400',
    bgColor: 'bg-yellow-500/10',
  },
  CODE: {
    label: 'Artefato de Código',
    shortLabel: 'Código',
    icon: FileCode,
    color: 'text-blue-400',
    bgColor: 'bg-blue-500/10',
  },
};
