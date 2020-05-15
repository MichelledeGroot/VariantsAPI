db.createUser(
    {
        user  : "JMT",
        pwd   : "variantsaapies",
        roles : [
        {
            role : "readWrite",
            db   : "variantsaapies"
        }
      ]
     }
   )

let error = true

let res = [
  db.variantsaapies.drop(),
  db.variantsaapies.drop(),
  db.variantsaapies.createIndex({ myfield: 1 }, { unique: true }),
  db.variantsaapies.createIndex({ thatfield: 1 }),
  db.variantsaapies.createIndex({ thatfield: 1 }),
  db.variantsaapies.insert({ myfield: 'hello', thatfield: 'testing' }),
  db.variantsaapies.insert({ myfield: 'hello2', thatfield: 'testing' }),
  db.variantsaapies.insert({ myfield: 'hello3', thatfield: 'testing' }),
  db.variantsaapies.insert({ myfield: 'hello3', thatfield: 'testing' }),
]

printjson(res)

if (error) {
  print('Error, exiting')
  quit(1)
}
