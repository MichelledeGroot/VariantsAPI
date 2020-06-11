'''
vcf_reader
Contains the logic for parsing the vcf and filtering pathogenic variants
'''

def file_to_vcfs(path_vcf):
    '''
    :param path_vcf: The patch of the variants vcf file
    :return: List of variants (filtered)
    '''
    with open(path_vcf) as file:
        variants = convert_file_to_variants(file)
    return variants


def convert_file_to_variants(file):
    '''
    Uses the opened file to parse and filter the variants
    :param file: Opened file
    :return: list with filtered variants
    '''
    variants = []
    for line in file:
        if line.startswith("#"):
            continue
        line = line.split("\t")
        info = dict(x.split("=") for x in line[7].split(";") if len(x.split("=")) == 2)
        if check_pathogenic(info):
            variant = {"Chromosome": line[0], "Position": line[1], "variant_id": line[2],
                       "Reference": line[3], "Alternate": line[4], "Info": info}
            variants.append(variant)

    return variants

def check_pathogenic(info):
    '''
    Returns True for all variants that meet the filter criteria
    :param info: dictionary with all info values
    :return: True for possible pathogenic variants, False for probably benign variants
    '''
    try:
        allele_frequency = float(info['AF'])
        allele_count = float(info['AC'])
        non_cancer_ac = float(info["non_cancer_AC"])
    except KeyError: #Allele frequency or non_cancer allele frequency is not known
        return False

    if allele_frequency > 0.01:
        #allele frequency is higher than 1% --> deemed benign
        return False
    elif allele_count - non_cancer_ac == 0:
        #allele_count - non_cancer ac (=cancer_ac) == 0 --> variant not present in cancer group
        # --> deemed benign
        return False
    return True
