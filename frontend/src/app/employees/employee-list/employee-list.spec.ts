import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { EmployeeList } from './employee-list';
import { EmployeeService } from '../employee.service';
import { Employee, FilterOptions, PageResponse } from '../employee.model';

describe('EmployeeList', () => {
  let component: EmployeeList;
  let fixture: ComponentFixture<EmployeeList>;
  let employeeService: {
    search: ReturnType<typeof vi.fn>;
    filterOptions: ReturnType<typeof vi.fn>;
    deactivate: ReturnType<typeof vi.fn>;
    reactivate: ReturnType<typeof vi.fn>;
  };
  let router: { navigate: ReturnType<typeof vi.fn> };

  const employee: Employee = {
    id: 1,
    employeeCode: 'ACME-000001',
    firstName: 'Ada',
    lastName: 'Lovelace',
    email: 'ada@acme.example',
    department: 'Engineering',
    jobTitle: 'Software Engineer',
    country: 'IN',
    currency: 'INR',
    status: 'ACTIVE',
    dateOfJoining: '2020-01-15',
    basicAmount: 50000,
    allowancesAmount: 12000,
    deductionsAmount: 3000,
    grossAmount: 62000,
    netAmount: 59000,
  };

  const page: PageResponse<Employee> = {
    content: [employee],
    page: 0,
    size: 20,
    totalElements: 1,
    totalPages: 1,
  };

  const filterOptions: FilterOptions = {
    departments: ['Engineering'],
    countries: ['IN'],
    jobTitles: ['Software Engineer'],
  };

  beforeEach(async () => {
    employeeService = {
      search: vi.fn().mockReturnValue(of(page)),
      filterOptions: vi.fn().mockReturnValue(of(filterOptions)),
      deactivate: vi.fn().mockReturnValue(of({ ...employee, status: 'INACTIVE' })),
      reactivate: vi.fn().mockReturnValue(of({ ...employee, status: 'ACTIVE' })),
    };
    router = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [EmployeeList],
      providers: [
        { provide: EmployeeService, useValue: employeeService },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeList);
    component = fixture.componentInstance;
  });

  it('loads filter options and the first page of employees on init', () => {
    fixture.detectChanges();

    expect(employeeService.filterOptions).toHaveBeenCalled();
    expect(employeeService.search).toHaveBeenCalledWith(
      expect.objectContaining({ page: 0, size: 20 }),
    );
    expect(component.employees()).toEqual([employee]);
    expect(component.totalElements()).toBe(1);
    expect(component.filterOptions()).toEqual(filterOptions);
  });

  it('resets to the first page and re-searches when a filter changes', () => {
    fixture.detectChanges();
    component.pageIndex.set(3);
    component.department.set('Engineering');

    component.onFilterChange();

    expect(component.pageIndex()).toBe(0);
    expect(employeeService.search).toHaveBeenLastCalledWith(
      expect.objectContaining({ department: 'Engineering', page: 0 }),
    );
  });

  it('re-searches with the new page and size on pagination', () => {
    fixture.detectChanges();

    component.onPageChange({ pageIndex: 2, pageSize: 50 } as any);

    expect(component.pageIndex()).toBe(2);
    expect(component.pageSize()).toBe(50);
    expect(employeeService.search).toHaveBeenLastCalledWith(
      expect.objectContaining({ page: 2, size: 50 }),
    );
  });

  it('navigates to the employee detail page when a row is opened', () => {
    fixture.detectChanges();

    component.viewEmployee(employee);

    expect(router.navigate).toHaveBeenCalledWith(['/employees', 1]);
  });

  it('navigates to the create form', () => {
    fixture.detectChanges();

    component.createEmployee();

    expect(router.navigate).toHaveBeenCalledWith(['/employees/new']);
  });

  it('deactivates an active employee and refreshes the list', () => {
    fixture.detectChanges();
    const event = { stopPropagation: vi.fn() } as unknown as Event;

    component.toggleStatus(employee, event);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(employeeService.deactivate).toHaveBeenCalledWith(1);
    expect(employeeService.search).toHaveBeenCalledTimes(2);
  });

  it('reactivates an inactive employee', () => {
    fixture.detectChanges();
    const inactiveEmployee: Employee = { ...employee, status: 'INACTIVE' };
    const event = { stopPropagation: vi.fn() } as unknown as Event;

    component.toggleStatus(inactiveEmployee, event);

    expect(employeeService.reactivate).toHaveBeenCalledWith(1);
  });
});
