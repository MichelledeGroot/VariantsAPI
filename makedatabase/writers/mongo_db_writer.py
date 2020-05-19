import pymongo

def make_variants_database(variants):
    myclient = pymongo.MongoClient("mongodb://database:27017/")
    variantsdb = myclient["variantsdatabase"]
    variantscol = variantsdb["variants"]
    x = variantscol.insert_many(variants)

