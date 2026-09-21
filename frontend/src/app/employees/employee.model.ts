export type EmployeeStatus = 'ACTIVE' | 'INACTIVE';

export interface Employee {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
  country: string;
  currency: string;
  status: EmployeeStatus;
  dateOfJoining: string;
  basicAmount: number;
  allowancesAmount: number;
  deductionsAmount: number;
  grossAmount: number;
  netAmount: number;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface FilterOptions {
  departments: string[];
  countries: string[];
  jobTitles: string[];
}

export interface EmployeeSearchParams {
  department?: string;
  country?: string;
  jobTitle?: string;
  status?: EmployeeStatus;
  query?: string;
  page?: number;
  size?: number;
}

export interface CreateEmployeeRequest {
  firstName: string;
  lastName: string;
  email: string;
  department: string;
  jobTitle: string;
  country: string;
  currency: string;
  dateOfJoining: string;
  basic: number;
  allowances: number;
  deductions: number;
}

export interface UpdateSalaryRequest {
  basic: number;
  allowances: number;
  deductions: number;
}
