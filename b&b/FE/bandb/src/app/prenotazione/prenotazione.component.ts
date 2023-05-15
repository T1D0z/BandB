import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { PrenotazioneService } from 'src/service/prenotazione.service';
import { Prenotazione } from 'src/service/prenotazione';
import { environment } from 'src/environments/environment';

declare var paypal;

@Component({
  selector: 'app-prenotazione',
  templateUrl: './prenotazione.component.html',
  styleUrls: ['./prenotazione.component.css']
})
export class PrenotazioneComponent implements OnInit{
  
  form:FormGroup;
  prenotazione: Prenotazione = new Prenotazione();
  prenotazioni: Prenotazione[];
  prenotaState: number = 0;
  numGiorni: number;
  success: boolean = false;
  details: any;
  roomPrice: number = environment.roomPrice;
  dataInizio: string;
  dataFine: string;

  @ViewChild('paypal', {static: true}) paymentElement : ElementRef;


   constructor(public fb:FormBuilder, private http: HttpClient, public PrenotazioneService: PrenotazioneService){

    this.form=fb.group({
      'nome': ['', Validators.required],
      'cognome': ['', Validators.required],
      'email': ['', Validators.required],
      'telefono': ['', Validators.pattern("^((\\+[0-9]{2}-?)|0)?[0-9]{10}$")],
      'camera': ['101', Validators.required],
      'data_inizio': ['', Validators.required],
      'data_fine': ['', Validators.required],
      'ospiti': ['1', Validators.required]
    })

    this.form.controls['telefono'].addValidators(Validators.required);

   }



  ngOnInit(): void {

    this.PrenotazioneService.getPrenotazione().subscribe(data => {
      this.prenotazioni = data;
    });

    

   }


  decrement(){
    const num = document.getElementById('num') as HTMLInputElement | null;
    
    if(num.valueAsNumber > 1){
      num.valueAsNumber--;
      this.form.get("ospiti").setValue(num.valueAsNumber);

    }
    
  }

  increment(){
    const num = document.getElementById('num') as HTMLInputElement | null;
  
    if(num.valueAsNumber < 3){
      num.valueAsNumber++;
      this.form.get("ospiti").setValue(num.valueAsNumber);

    }
    
  }

  isBefore(date1: Date, date2: Date) {
    return date1 <= date2;
  }
  
  isAfter(date1: Date, date2: Date) {
    return date1 >= date2;
  }

  isInRange(date_I: Date, date: Date, date_F: Date){
    return (this.isAfter(date, date_I) && this.isBefore(date, date_F));
  }


  myFilter = (d: Date): boolean => {
    /*
      TRUE - DISPONIBILE
      FALSE - DISABILITATA
    */
    var filtered: boolean = true;
    var checked: boolean = false;
    var camera = this.form.controls['camera'].value;

    this.prenotazioni.forEach(element => {
      if(camera == element.camera){
        if(!checked){
         if(this.isInRange(new Date(element.data_inizio), d, new Date(element.data_fine))){
           checked = true;
            filtered = false;
          } else {
          filtered = true;
          }
        }
      }
    });
    
    if(this.isBefore(d, new Date())){
      filtered = false;
    }

    return filtered;
  }

  callPrenota() {
    this.prenotaState = 3;
    this.PrenotazioneService.addPrenotazione(this.prenotazione).subscribe();
  }

  renderPaypal() {
    this.prenotaState = 2;
    const total: number = this.roomPrice * this.prenotazione.ospiti * this.numGiorni;

    paypal.Buttons({
      createOrder: function (data, actions) {
        return actions.order.create({
          purchase_units: [{
            amount: {
              value: total
            }
          }]
        });
      },
      onApprove: (data, actions) => {
        return actions.order.capture().then(function (details) {
        }).then(this.callPrenota())
      },
      onCancel: function(data) {
        window.location.href = "/index.html";
      }
    }).render(this.paymentElement.nativeElement);

  }

  redirect() { window.setTimeout(function(){ window.location.href = "/index.html"; }, 5000); }

  send() {
    this.prenotazione = this.form.value;
    this.dataInizio = this.prenotazione.data_inizio.toISOString().slice(0,10);
    this.dataFine = this.prenotazione.data_fine.toISOString().slice(0,10);
    this.numGiorni = Math.ceil(Math.abs(this.prenotazione.data_inizio.valueOf() - this.prenotazione.data_fine.valueOf()) / (1000 * 3600 * 24) );
    this.prenotaState = 1;
  }
}
