import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { DashboardService } from './dashboard.service';
import { CurrencyAwareStats, GroupedSalaryStats } from './dashboard.model';

describe('DashboardService', () => {
  let service: DashboardService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DashboardService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(DashboardService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('summary sends only the provided filters as query params', () => {
    const response: CurrencyAwareStats = {
      stats: { count: 1, min: 50000, max: 50000, average: 50000, median: 50000 },
      currency: 'INR',
    };

    service
      .summary({ department: 'Engineering', country: 'IN' })
      .subscribe((result) => expect(result).toEqual(response));

    const req = httpMock.expectOne(
      (r) =>
        r.url === '/api/dashboard/summary' &&
        r.params.get('department') === 'Engineering' &&
        r.params.get('country') === 'IN' &&
        !r.params.has('jobTitle'),
    );
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });

  it('breakdown sends the requested dimension', () => {
    const response: GroupedSalaryStats[] = [
      {
        group: 'Engineering',
        stats: { stats: { count: 1, min: 1, max: 1, average: 1, median: 1 }, currency: 'INR' },
      },
    ];

    service.breakdown('DEPARTMENT').subscribe((result) => expect(result).toEqual(response));

    const req = httpMock.expectOne((r) => r.url === '/api/dashboard/breakdown' && r.params.get('by') === 'DEPARTMENT');
    expect(req.request.method).toBe('GET');
    req.flush(response);
  });
});
