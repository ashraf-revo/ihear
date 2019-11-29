import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JoyStickComponent } from './joy-stick.component';

describe('JoyStickComponent', () => {
  let component: JoyStickComponent;
  let fixture: ComponentFixture<JoyStickComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JoyStickComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JoyStickComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
