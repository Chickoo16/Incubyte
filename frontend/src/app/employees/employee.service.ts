import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  CreateEmployeeRequest,
  Employee,
  EmployeeSearchParams,
  FilterOptions,
  PageResponse,
  UpdateSalaryRequest,
} from './employee.model';

const BASE_URL = '/api/employees';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly http = inject(HttpClient);

  search(params: EmployeeSearchParams): Observable<PageResponse<Employee>> {
    let httpParams = new HttpParams();
    for (const [key, value] of Object.entries(params)) {
      if (value !== undefined && value !== null && value !== '') {
        httpParams = httpParams.set(key, String(value));
      }
    }
    return this.http.get<PageResponse<Employee>>(BASE_URL, { params: httpParams });
  }

  getById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${BASE_URL}/${id}`);
  }

  create(request: CreateEmployeeRequest): Observable<Employee> {
    return this.http.post<Employee>(BASE_URL, request);
  }

  updateSalary(id: number, request: UpdateSalaryRequest): Observable<Employee> {
    return this.http.put<Employee>(`${BASE_URL}/${id}/salary`, request);
  }

  deactivate(id: number): Observable<Employee> {
    return this.http.post<Employee>(`${BASE_URL}/${id}/deactivate`, {});
  }

  reactivate(id: number): Observable<Employee> {
    return this.http.post<Employee>(`${BASE_URL}/${id}/reactivate`, {});
  }

  filterOptions(): Observable<FilterOptions> {
    return this.http.get<FilterOptions>(`${BASE_URL}/filters`);
  }
}
