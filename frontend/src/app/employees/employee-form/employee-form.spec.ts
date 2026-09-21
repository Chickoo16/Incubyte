import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { EmployeeForm } from './employee-form';
import { EmployeeService } from '../employee.service';
import { Employee } from '../employee.model';

describe('EmployeeForm', () => {
  let component: EmployeeForm;
  let fixture: ComponentFixture<EmployeeForm>;
  let employeeService: { create: ReturnType<typeof vi.fn> };
  let router: { navigate: ReturnType<typeof vi.fn> };

  const created: Employee = {
    id: 42,
    employeeCode: 'ACME-000042',
    firstName: 'Grace',
    lastName: 'Hopper',
    email: 'grace@acme.example',
    department: 'Engineering',
    jobTitle: 'Principal Engineer',
    country: 'US',
    currency: 'USD',
    status: 'ACTIVE',
    dateOfJoining: '2019-03-01',
    basicAmount: 90000,
    allowancesAmount: 5000,
    deductionsAmount: 10000,
    grossAmount: 95000,
    netAmount: 85000,
  };

  function fillValidForm(): void {
    component.form.setValue({
      firstName: 'Grace',
      lastName: 'Hopper',
      email: 'grace@acme.example',
      department: 'Engineering',
      jobTitle: 'Principal Engineer',
      country: 'US',
      currency: 'USD',
      dateOfJoining: '2019-03-01',
      basic: 90000,
      allowances: 5000,
      deductions: 10000,
    });
  }

  beforeEach(async () => {
    employeeService = { create: vi.fn().mockReturnValue(of(created)) };
    router = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [EmployeeForm],
      providers: [
        { provide: EmployeeService, useValue: employeeService },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('does not submit an invalid form', () => {
    component.submit();

    expect(employeeService.create).not.toHaveBeenCalled();
    expect(component.form.touched).toBe(true);
  });

  it('creates the employee and navigates to their detail page on success', () => {
    fillValidForm();

    component.submit();

    expect(employeeService.create).toHaveBeenCalledWith({
      firstName: 'Grace',
      lastName: 'Hopper',
      email: 'grace@acme.example',
      department: 'Engineering',
      jobTitle: 'Principal Engineer',
      country: 'US',
      currency: 'USD',
      dateOfJoining: '2019-03-01',
      basic: 90000,
      allowances: 5000,
      deductions: 10000,
    });
    expect(router.navigate).toHaveBeenCalledWith(['/employees', 42]);
  });

  it('surfaces an error and stops saving when creation fails', () => {
    employeeService.create.mockReturnValue(throwError(() => new Error('boom')));
    fillValidForm();

    component.submit();

    expect(component.saving()).toBe(false);
    expect(component.error()).toBeTruthy();
  });

  it('cancel navigates back to the list', () => {
    component.cancel();

    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
