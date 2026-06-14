import type { DecisionTable } from '../types/decisionTable';

const STORAGE_PREFIX = 'decision_table_gce_';

function buildKey(gceId: string): string {
  return `${STORAGE_PREFIX}${gceId}`;
}

const decisionTableLocalService = {
  /**
   * Persists the decision table to localStorage, stamping updatedAt.
   */
  save(table: DecisionTable): DecisionTable {
    const updated: DecisionTable = { ...table, updatedAt: new Date().toISOString() };
    localStorage.setItem(buildKey(table.gceId), JSON.stringify(updated));
    return updated;
  },

  /**
   * Loads the persisted table for a given GCE, or null if none exists.
   */
  getByGceId(gceId: string): DecisionTable | null {
    const raw = localStorage.getItem(buildKey(gceId));
    if (!raw) return null;
    try {
      return JSON.parse(raw) as DecisionTable;
    } catch {
      return null;
    }
  },

  deleteByGceId(gceId: string): void {
    localStorage.removeItem(buildKey(gceId));
  },
};

export default decisionTableLocalService;
