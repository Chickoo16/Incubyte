import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { EmployeeDetail } from './employee-detail';
import { EmployeeService } from '../employee.service';
import { Employee } from '../employee.model';

describe('EmployeeDetail', () => {
  let component: EmployeeDetail;
  let fixture: ComponentFixture<EmployeeDetail>;
  let employeeService: {
    getById: ReturnType<typeof vi.fn>;
    updateSalary: ReturnType<typeof vi.fn>;
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

  beforeEach(async () => {
    employeeService = {
      getById: vi.fn().mockReturnValue(of(employee)),
      updateSalary: vi.fn().mockReturnValue(of({ ...employee, basicAmount: 60000, grossAmount: 72000, netAmount: 69000 })),
      deactivate: vi.fn().mockReturnValue(of({ ...employee, status: 'INACTIVE' })),
      reactivate: vi.fn().mockReturnValue(of({ ...employee, status: 'ACTIVE' })),
    };
    router = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [EmployeeDetail],
      providers: [
        { provide: EmployeeService, useValue: employeeService },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeDetail);
    component = fixture.componentInstance;
  });

  it('loads the employee and seeds the salary form from it', () => {
    fixture.detectChanges();

    expect(employeeService.getById).toHaveBeenCalledWith(1);
    expect(component.employee()).toEqual(employee);
    expect(component.basic()).toBe(50000);
    expect(component.allowances()).toBe(12000);
    expect(component.deductions()).toBe(3000);
  });

  it('previews gross and net as the form values change', () => {
    fixture.detectChanges();

    component.basic.set(60000);
    component.allowances.set(10000);
    component.deductions.set(5000);

    expect(component.previewGross()).toBe(70000);
    expect(component.previewNet()).toBe(65000);
  });

  it('saves the salary with the current form values', () => {
    fixture.detectChanges();
    component.basic.set(60000);
    component.allowances.set(12000);
    component.deductions.set(3000);

    component.saveSalary();

    expect(employeeService.updateSalary).toHaveBeenCalledWith(1, {
      basic: 60000,
      allowances: 12000,
      deductions: 3000,
    });
    expect(component.employee()?.basicAmount).toBe(60000);
  });

  it('deactivates an active employee', () => {
    fixture.detectChanges();

    component.toggleStatus();

    expect(employeeService.deactivate).toHaveBeenCalledWith(1);
    expect(component.employee()?.status).toBe('INACTIVE');
  });

  it('navigates back to the list', () => {
    fixture.detectChanges();

    component.back();

    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
