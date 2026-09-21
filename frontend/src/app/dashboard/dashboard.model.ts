export interface SalaryStats {
  count: number;
  min: number;
  max: number;
  average: number;
  median: number;
}

export interface CurrencyAwareStats {
  stats: SalaryStats;
  currency: string | null;
}

export const MIXED_CURRENCY = 'MIXED';

export interface GroupedSalaryStats {
  group: string;
  stats: CurrencyAwareStats;
}

export type GroupDimension = 'DEPARTMENT' | 'COUNTRY' | 'JOB_TITLE';

export interface SummaryParams {
  department?: string;
  country?: string;
  jobTitle?: string;
}
