import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { CurrencyAwareStats, GroupDimension, GroupedSalaryStats, SummaryParams } from './dashboard.model';

const BASE_URL = '/api/dashboard';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);

  summary(params: SummaryParams): Observable<CurrencyAwareStats> {
    let httpParams = new HttpParams();
    for (const [key, value] of Object.entries(params)) {
      if (value) {
        httpParams = httpParams.set(key, value);
      }
    }
    return this.http.get<CurrencyAwareStats>(`${BASE_URL}/summary`, { params: httpParams });
  }

  breakdown(by: GroupDimension): Observable<GroupedSalaryStats[]> {
    return this.http.get<GroupedSalaryStats[]>(`${BASE_URL}/breakdown`, { params: { by } });
  }
}
