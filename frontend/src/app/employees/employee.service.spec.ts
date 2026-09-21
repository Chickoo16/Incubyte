import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { EmployeeService } from './employee.service';
import { Employee, PageResponse } from './employee.model';

describe('EmployeeService', () => {
  let service: EmployeeService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [EmployeeService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(EmployeeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  const sampleEmployee: Employee = {
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

  it('search sends filters, query and pagination as query params', () => {
    const page: PageResponse<Employee> = {
      content: [sampleEmployee],
      page: 0,
      size: 20,
      totalElements: 1,
      totalPages: 1,
    };

    service
      .search({ department: 'Engineering', status: 'ACTIVE', query: 'ada', page: 0, size: 20 })
      .subscribe((result) => expect(result).toEqual(page));

    const req = httpMock.expectOne(
      (r) =>
        r.url === '/api/employees' &&
        r.params.get('department') === 'Engineering' &&
        r.params.get('status') === 'ACTIVE' &&
        r.params.get('query') === 'ada' &&
        r.params.get('page') === '0' &&
        r.params.get('size') === '20',
    );
    expect(req.request.method).toBe('GET');
    req.flush(page);
  });

  it('search omits params that are not provided', () => {
    service.search({}).subscribe();

    const req = httpMock.expectOne(() => true);
    expect(req.request.params.keys().length).toBe(0);
    req.flush({ content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 });
  });

  it('getById fetches a single employee', () => {
    service.getById(1).subscribe((result) => expect(result).toEqual(sampleEmployee));

    const req = httpMock.expectOne('/api/employees/1');
    expect(req.request.method).toBe('GET');
    req.flush(sampleEmployee);
  });

  it('create posts a new employee', () => {
    service
      .create({
        firstName: 'Ada',
        lastName: 'Lovelace',
        email: 'ada@acme.example',
        department: 'Engineering',
        jobTitle: 'Software Engineer',
        country: 'IN',
        currency: 'INR',
        dateOfJoining: '2020-01-15',
        basic: 50000,
        allowances: 12000,
        deductions: 3000,
      })
      .subscribe((result) => expect(result).toEqual(sampleEmployee));

    const req = httpMock.expectOne('/api/employees');
    expect(req.request.method).toBe('POST');
    req.flush(sampleEmployee);
  });

  it('updateSalary puts to the salary endpoint', () => {
    service
      .updateSalary(1, { basic: 60000, allowances: 10000, deductions: 5000 })
      .subscribe((result) => expect(result).toEqual(sampleEmployee));

    const req = httpMock.expectOne('/api/employees/1/salary');
    expect(req.request.method).toBe('PUT');
    req.flush(sampleEmployee);
  });

  it('deactivate posts to the deactivate endpoint', () => {
    service.deactivate(1).subscribe((result) => expect(result).toEqual(sampleEmployee));

    const req = httpMock.expectOne('/api/employees/1/deactivate');
    expect(req.request.method).toBe('POST');
    req.flush(sampleEmployee);
  });

  it('reactivate posts to the reactivate endpoint', () => {
    service.reactivate(1).subscribe((result) => expect(result).toEqual(sampleEmployee));

    const req = httpMock.expectOne('/api/employees/1/reactivate');
    expect(req.request.method).toBe('POST');
    req.flush(sampleEmployee);
  });

  it('filterOptions fetches the dropdown options', () => {
    const options = { departments: ['Engineering'], countries: ['IN'], jobTitles: ['Engineer'] };
    service.filterOptions().subscribe((result) => expect(result).toEqual(options));

    const req = httpMock.expectOne('/api/employees/filters');
    expect(req.request.method).toBe('GET');
    req.flush(options);
  });
});
