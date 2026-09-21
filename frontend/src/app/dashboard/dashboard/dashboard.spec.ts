import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { Dashboard } from './dashboard';
import { DashboardService } from '../dashboard.service';
import { EmployeeService } from '../../employees/employee.service';
import { CurrencyAwareStats, GroupedSalaryStats } from '../dashboard.model';
import { FilterOptions } from '../../employees/employee.model';

describe('Dashboard', () => {
  let component: Dashboard;
  let fixture: ComponentFixture<Dashboard>;
  let dashboardService: { summary: ReturnType<typeof vi.fn>; breakdown: ReturnType<typeof vi.fn> };
  let employeeService: { filterOptions: ReturnType<typeof vi.fn> };

  const mixedSummary: CurrencyAwareStats = {
    stats: { count: 10000, min: 30000, max: 4000000, average: 280000, median: 90000 },
    currency: 'MIXED',
  };

  const singleCurrencySummary: CurrencyAwareStats = {
    stats: { count: 1956, min: 468203, max: 4123743, average: 1055992, median: 853214 },
    currency: 'INR',
  };

  const breakdown: GroupedSalaryStats[] = [
    { group: 'IN', stats: singleCurrencySummary },
  ];

  const filterOptions: FilterOptions = {
    departments: ['Engineering'],
    countries: ['IN'],
    jobTitles: ['Engineer'],
  };

  beforeEach(async () => {
    dashboardService = {
      summary: vi.fn().mockReturnValue(of(mixedSummary)),
      breakdown: vi.fn().mockReturnValue(of(breakdown)),
    };
    employeeService = { filterOptions: vi.fn().mockReturnValue(of(filterOptions)) };

    await TestBed.configureTestingModule({
      imports: [Dashboard],
      providers: [
        { provide: DashboardService, useValue: dashboardService },
        { provide: EmployeeService, useValue: employeeService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Dashboard);
    component = fixture.componentInstance;
  });

  it('loads filter options, summary and the default breakdown on init', () => {
    fixture.detectChanges();

    expect(employeeService.filterOptions).toHaveBeenCalled();
    expect(dashboardService.summary).toHaveBeenCalledWith({
      department: undefined,
      country: undefined,
      jobTitle: undefined,
    });
    expect(dashboardService.breakdown).toHaveBeenCalledWith('DEPARTMENT');
    expect(component.summary()).toEqual(mixedSummary);
    expect(component.breakdown()).toEqual(breakdown);
  });

  it('flags mixed-currency summaries', () => {
    fixture.detectChanges();

    expect(component.isMixedCurrency()).toBe(true);
  });

  it('does not flag a single-currency summary', () => {
    dashboardService.summary.mockReturnValue(of(singleCurrencySummary));
    fixture.detectChanges();

    expect(component.isMixedCurrency()).toBe(false);
  });

  it('reloads the summary with current filters on filter change', () => {
    fixture.detectChanges();
    component.country.set('IN');

    component.onFilterChange();

    expect(dashboardService.summary).toHaveBeenLastCalledWith({
      department: undefined,
      country: 'IN',
      jobTitle: undefined,
    });
  });

  it('reloads the breakdown when the dimension changes', () => {
    fixture.detectChanges();

    component.onDimensionChange('COUNTRY');

    expect(component.dimension()).toBe('COUNTRY');
    expect(dashboardService.breakdown).toHaveBeenLastCalledWith('COUNTRY');
  });

  it('flags a breakdown row whose currency is mixed, so its numbers can be hidden', () => {
    fixture.detectChanges();

    expect(component.isMixedRow({ group: 'Engineering', stats: mixedSummary })).toBe(true);
    expect(component.isMixedRow({ group: 'IN', stats: singleCurrencySummary })).toBe(false);
  });
});
