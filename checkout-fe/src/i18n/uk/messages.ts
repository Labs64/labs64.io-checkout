export default {
  common: {
    checkout: 'Оформлення замовлення',
    change: 'Змінити',
    buy: 'Купити зараз',

    form: {
      firstName: 'Ім’я',
      lastName: 'Прізвище',
      email: 'Електронна пошта',
      phone: 'Телефон',
      vatId: 'ПДВ-номер',
      country: 'Країна',
      city: 'Місто',
      state: 'Штат / регіон',
      postalCode: 'Поштовий індекс',
      address1: 'Адреса, рядок 1',
      address2: 'Адреса, рядок 2',
    },
  },

  order: {
    title: 'Замовлення',

    summary: {
      itemsTotal:
        'немає товарі | {count} товар на суму {total} | {count} товара на суму {total} | {count} товарів на суму {total}',
      subtotal: 'Проміжний підсумок (без податку)',
      tax: 'Податок',
      total: 'Разом до сплати',
    },
  },

  billing: {
    title: 'Платіжні дані',
    saveDetails: 'Зберегти платіжні дані на цьому пристрої',
    copyFromShipping: 'Скопіювати з даних доставки',
  },

  shipping: {
    title: 'Доставка',
    saveDetails: 'Зберегти дані доставки на цьому пристрої',
    copyFromBilling: 'Скопіювати з платіжних даних',
  },

  payment: {
    title: 'Оплата',
  },

  error: {
    notFound: {
      title: 'Помилка 404',
      description: 'Сторінка, яку ви шукаєте, була переміщена, видалена або, можливо, ніколи не існувала.',
      back: 'Повернутися назад',
    },
  },
};
