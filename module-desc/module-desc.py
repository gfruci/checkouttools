import shutil
import sys
import tempfile
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

NAME_SPACES = {'spring': "http://www.springframework.org/schema/beans",
               'util': "http://www.springframework.org/schema/util"}
DEPENDS_ON_MODULE_KEY = 'depends_on_modules'
EXTERNAL_DEPENDENCY_BEANS_KEY = 'externalDependencies'
PUBLIC_BEANS_KEY = 'publicBeans'


debug = False
modules = {}


def log(s, *args, **kwargs):
    print(s.format(args, kwargs))


def debug_log(s, *args, **kwargs):
    if debug:
        print(s.format(args, kwargs))
    pass


def parse_module_descriptors(path):
    log("Parse module descriptors in {}", path)
    if not path:
        log("No path specified")
        return
    for f in Path(path).glob("**/*module-descriptor.xml"):
        debug_log("Parse file: {}", str(f))
        dom = ET.parse(str(f)).getroot()
        xml_file = str(f).split("\\")[-1]
        modules[xml_file] = {}
        for i in dom.findall("util:set", NAME_SPACES):
            modules[xml_file][i.get("id")] = [e.text for e in list(i)]
    pass


def extract_module_descriptor_from_jars(path):
    log("Extract module descriptors from jars")
    jars = list(Path(path).glob("**/*.jar"))

    if not jars:
        log("No jar files found in {}", path)
        return

    temp_dir = tempfile.mkdtemp()
    log("Temp dir created: {}", str(temp_dir))

    for j in jars:
        zip_file = zipfile.ZipFile(str(j), "r")
        zip_file.extractall(temp_dir,
                            [name for name in zip_file.namelist() if name.endswith("module-descriptor.xml")])
    return temp_dir


def calculate_dependencies():
    for (k, v) in modules.items():
        for external_dep in v.get(EXTERNAL_DEPENDENCY_BEANS_KEY, []):
            if DEPENDS_ON_MODULE_KEY not in modules[k]:
                modules[k][DEPENDS_ON_MODULE_KEY] = set()
            modules[k][DEPENDS_ON_MODULE_KEY].add(find_dependency(external_dep))
    pass


def find_dependency(dep):
    for (k, v) in modules.items():
        if dep in v.get(PUBLIC_BEANS_KEY, []):
            return k
    return "NotFound - " + dep


def print_dependency_tree(mod_name, indent_level=0):
    if mod_name:
        if mod_name in modules.keys():
            print(("\t" * indent_level) + mod_name)
            for dep_name in modules[mod_name].get(DEPENDS_ON_MODULE_KEY, []):
                print_dependency_tree(dep_name, indent_level + 1)
    else:
        for (key, value) in modules.items():
            print(key)
            print("\t" + "\n\t".join(value.get(DEPENDS_ON_MODULE_KEY, [])))
    pass


if __name__ == '__main__':
    base_dir = sys.argv[1]
    module_name = sys.argv[2] if len(sys.argv) > 2 else None

    parse_module_descriptors(base_dir)

    extracted_descriptors = extract_module_descriptor_from_jars(base_dir)
    if extracted_descriptors:
        parse_module_descriptors(extracted_descriptors)

    calculate_dependencies()

    shutil.rmtree(extracted_descriptors, ignore_errors=True)
    print_dependency_tree(module_name)
