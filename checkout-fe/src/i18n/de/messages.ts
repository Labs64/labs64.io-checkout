export default {
  common: {
    checkout: 'Zur Kasse',
    change: 'Ändern',
    buy: 'Jetzt kaufen',

    form: {
      firstName: 'Vorname',
      lastName: 'Nachname',
      email: 'E-Mail',
      phone: 'Telefonnummer',
      vatId: 'USt-IdNr.',
      country: 'Land',
      city: 'Stadt',
      state: 'Bundesland / Region',
      postalCode: 'Postleitzahl',
      address1: 'Adresszeile 1',
      address2: 'Adresszeile 2',
    },
  },

  order: {
    title: 'Bestellung',

    summary: {
      itemsTotal: '{count} Artikel im Wert von {total} | {count} Artikel im Wert von {total}',
      subtotal: 'Zwischensumme (exkl. Steuern)', // netAmount
      tax: 'Steuern',
      total: 'Gesamtbetrag',
    },
  },

  billing: {
    title: 'Rechnungsdaten',
    saveDetails: 'Rechnungsdaten auf diesem Gerät speichern',
    copyFromShipping: 'Aus Versanddaten übernehmen',
  },

  shipping: {
    title: 'Versand',
    saveDetails: 'Versanddaten auf diesem Gerät speichern',
    copyFromBilling: 'Aus Rechnungsdaten übernehmen',
  },

  payment: {
    title: 'Zahlung',
  },

  error: {
    notFound: {
      title: 'Fehler 404',
      description: 'Die Seite, die Sie suchen, wurde verschoben, entfernt oder hat möglicherweise nie existiert.',
      back: 'Zurück',
    },
  },
};
